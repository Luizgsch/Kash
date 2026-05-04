import { prisma } from "@/lib/prisma";
import { NextRequest, NextResponse } from "next/server";
import { verifyMobileToken } from "../_lib/verifyMobileToken";

export async function POST(req: NextRequest) {
  try {
    const claims = await verifyMobileToken(req);
    const body   = await req.json();
    const items: typeof body[] = Array.isArray(body) ? body : [body];

    const results = await Promise.allSettled(
      items.map((l) =>
        prisma.loss.upsert({
          where: { id: l.id },
          create: {
            id:             l.id,
            productId:      l.productId,
            quantity:       l.quantity,
            reason:         l.reason ?? "",
            walletId:       l.walletId,
            organizationId: claims.orgId,
            createdAt:      new Date(l.createdAt),
          },
          update: {},  // perdas são imutáveis após sync
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
