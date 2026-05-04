import { prisma } from "@/lib/prisma";
import { NextRequest, NextResponse } from "next/server";
import { verifyMobileToken } from "../_lib/verifyMobileToken";

export async function POST(req: NextRequest) {
  try {
    const claims = await verifyMobileToken(req);
    const body   = await req.json();
    const items: typeof body[] = Array.isArray(body) ? body : [body];

    const results = await Promise.allSettled(
      items.map((p) =>
        prisma.product.upsert({
          where: { id: p.id },
          create: {
            id:             p.id,
            name:           p.name,
            walletId:       p.walletId,
            organizationId: claims.orgId,
            categoryId:     p.categoryId ?? null,
            salePriceCents: p.salePriceCents,
            costPriceCents: p.costPriceCents,
            currentStock:   p.currentStock ?? 0,
          },
          update: {
            name:           p.name,
            salePriceCents: p.salePriceCents,
            costPriceCents: p.costPriceCents,
            currentStock:   p.currentStock ?? 0,
            updatedAt:      new Date(),
          },
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

// GET: listar produtos da org (para sincronização inicial)
export async function GET(req: NextRequest) {
  try {
    const claims   = await verifyMobileToken(req);
    const walletId = req.nextUrl.searchParams.get("walletId");

    const products = await prisma.product.findMany({
      where: {
        organizationId: claims.orgId,
        ...(walletId ? { walletId } : {}),
      },
      orderBy: { name: "asc" },
    });

    return NextResponse.json(products);
  } catch (err: unknown) {
    const message = err instanceof Error ? err.message : "Erro";
    const status  = message.includes("Token") ? 401 : 500;
    return NextResponse.json({ error: message }, { status });
  }
}
