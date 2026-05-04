import { prisma } from "@/lib/prisma";
import { NextRequest, NextResponse } from "next/server";
import { verifyMobileToken } from "../_lib/verifyMobileToken";

export async function GET(req: NextRequest) {
  try {
    const claims = await verifyMobileToken(req);
    const { searchParams } = new URL(req.url);
    const walletId = searchParams.get("walletId");
    const limit    = Math.min(parseInt(searchParams.get("limit") ?? "50"), 200);

    const transactions = await prisma.transaction.findMany({
      where: {
        organizationId: claims.orgId,
        ...(walletId ? { walletId } : {}),
      },
      select: {
        id: true,
        amount: true,
        type: true,
        description: true,
        walletId: true,
        createdAt: true,
        category: { select: { id: true, name: true } },
      },
      orderBy: { createdAt: "desc" },
      take: limit,
    });

    return NextResponse.json(
      transactions.map((t) => ({
        id:          t.id,
        amountCents: t.amount,
        type:        t.type,
        description: t.description,
        categoryId:  t.category?.id ?? null,
        categoryName:t.category?.name ?? "Sem categoria",
        walletId:    t.walletId,
        createdAt:   t.createdAt.getTime(),
      }))
    );
  } catch (err: unknown) {
    const message = err instanceof Error ? err.message : "Erro";
    const status  = message.includes("Token") ? 401 : 500;
    return NextResponse.json({ error: message }, { status });
  }
}

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
