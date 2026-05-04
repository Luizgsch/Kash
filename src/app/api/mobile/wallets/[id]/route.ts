import { prisma } from "@/lib/prisma";
import { NextRequest, NextResponse } from "next/server";
import { verifyMobileToken } from "../../_lib/verifyMobileToken";

const NAME_MAX = 120;

export async function PATCH(
  req: NextRequest,
  { params }: { params: { id: string } }
) {
  try {
    const claims = await verifyMobileToken(req);
    const body = await req.json();

    const name = (body.name ?? "").trim().slice(0, NAME_MAX);
    if (!name) return NextResponse.json({ error: "Nome obrigatório." }, { status: 400 });

    const row = await prisma.wallet.findFirst({
      where: { id: params.id, organizationId: claims.orgId },
      select: { id: true },
    });
    if (!row) return NextResponse.json({ error: "Espaço não encontrado." }, { status: 404 });

    const updated = await prisma.wallet.update({
      where: { id: row.id },
      data: { name },
      select: { id: true, name: true },
    });

    return NextResponse.json(updated);
  } catch (err: unknown) {
    const message = err instanceof Error ? err.message : "Erro";
    const status = message.includes("Token") ? 401 : 500;
    return NextResponse.json({ error: message }, { status });
  }
}

export async function DELETE(
  req: NextRequest,
  { params }: { params: { id: string } }
) {
  try {
    const claims = await verifyMobileToken(req);

    const row = await prisma.wallet.findFirst({
      where: { id: params.id, organizationId: claims.orgId },
      select: { id: true },
    });
    if (!row) return NextResponse.json({ error: "Espaço não encontrado." }, { status: 404 });

    await prisma.$transaction([
      prisma.transaction.deleteMany({ where: { walletId: row.id } }),
      prisma.category.deleteMany({ where: { walletId: row.id } }),
      prisma.wallet.delete({ where: { id: row.id } }),
    ]);

    return NextResponse.json({ ok: true });
  } catch (err: unknown) {
    const message = err instanceof Error ? err.message : "Erro";
    const status = message.includes("Token") ? 401 : 500;
    return NextResponse.json({ error: message }, { status });
  }
}
