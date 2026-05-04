import { prisma } from "@/lib/prisma";
import { NextRequest, NextResponse } from "next/server";
import { verifyMobileToken } from "../_lib/verifyMobileToken";

export async function POST(req: NextRequest) {
  try {
    const claims = await verifyMobileToken(req);
    const body   = await req.json();
    const items: typeof body[] = Array.isArray(body) ? body : [body];

    const results = await Promise.allSettled(
      items.map((s) =>
        prisma.sale.upsert({
          where: { id: s.id },
          create: {
            id:                 s.id,
            productId:          s.productId,
            transactionId:      s.transactionId,
            quantity:           s.quantity,
            salePriceCentsEach: s.salePriceCentsEach,
            costPriceCentsEach: s.costPriceCentsEach,
            walletId:           s.walletId,
            organizationId:     claims.orgId,
            createdAt:          new Date(s.createdAt),
          },
          update: {},  // vendas são imutáveis após sync
        })
      )
    );

    const failed = results.filter((r) => r.status === "rejected");
    return NextResponse.json({ synced: results.length - failed.length, failed: failed.length });
  } catch (err: unknown) {
    const message = err instanceof Error ? err.message : "Erro";
    const status  = message.includes("Token") ? 401 : 500;
    return NextResponse.json({ error: message }, { status });
  }
}
