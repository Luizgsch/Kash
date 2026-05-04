import { prisma } from "@/lib/prisma";
import { NextRequest, NextResponse } from "next/server";
import { verifyMobileToken } from "../_lib/verifyMobileToken";

export async function POST(req: NextRequest) {
  try {
    const claims = await verifyMobileToken(req);
    const body   = await req.json();

    // Suporta array (batch) ou objeto único
    const items: typeof body[] = Array.isArray(body) ? body : [body];

    const results = await Promise.allSettled(
      items.map(async (tx) => {
        // Garante que categoryId pertence ao walletId correto
        let categoryId: string | undefined = tx.categoryId ?? undefined;

        if (!categoryId) {
          // Cria ou reutiliza categoria padrão "Mobile" para transações sem categoria
          const cat = await prisma.category.upsert({
            where: { walletId_name: { walletId: tx.walletId, name: "Mobile" } },
            create: { name: "Mobile", walletId: tx.walletId },
            update: {},
          });
          categoryId = cat.id;
        }

        return prisma.transaction.upsert({
          where: { id: tx.id },
          create: {
            id:             tx.id,
            amount:         tx.amountCents,
            type:           tx.type,
            description:    tx.description ?? "",
            categoryId,
            walletId:       tx.walletId,
            organizationId: claims.orgId,
            userId:         claims.sub,
            createdAt:      new Date(tx.createdAt),
          },
          update: {
            amount:      tx.amountCents,
            description: tx.description ?? "",
            categoryId,
            updatedAt:   new Date(),
          },
        });
      })
    );

    const failed = results.filter((r) => r.status === "rejected");
    if (failed.length > 0) {
      console.error("[mobile/transactions] partial failure", failed);
    }

    return NextResponse.json({ synced: results.length - failed.length, failed: failed.length });
  } catch (err: unknown) {
    const message = err instanceof Error ? err.message : "Erro";
    const status  = message.includes("Token") ? 401 : 500;
    return NextResponse.json({ error: message }, { status });
  }
}
