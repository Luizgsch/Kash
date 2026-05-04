import { prisma } from "@/lib/prisma";
import { NextRequest, NextResponse } from "next/server";
import { verifyMobileToken } from "../../_lib/verifyMobileToken";

export async function PATCH(
  req: NextRequest,
  { params }: { params: { id: string } }
) {
  try {
    const claims = await verifyMobileToken(req);
    const body = await req.json();
    const txId = params.id;

    const existing = await prisma.transaction.findFirst({
      where: { id: txId, organizationId: claims.orgId },
    });

    if (!existing) {
      return NextResponse.json({ error: "Transação não encontrada" }, { status: 404 });
    }

    let categoryId = body.categoryId ?? existing.categoryId;
    if (body.categoryId === null) {
      const cat = await prisma.category.upsert({
        where: { walletId_name: { walletId: existing.walletId, name: "Mobile" } },
        create: { name: "Mobile", walletId: existing.walletId },
        update: {},
      });
      categoryId = cat.id;
    }

    const updated = await prisma.transaction.update({
      where: { id: txId },
      data: {
        amount:      body.amountCents ?? existing.amount,
        type:        body.type ?? existing.type,
        description: body.description ?? existing.description ?? "",
        categoryId,
        updatedAt:   new Date(),
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
    });

    return NextResponse.json({
      id:          updated.id,
      amountCents: updated.amount,
      type:        updated.type,
      description: updated.description,
      categoryId:  updated.category?.id ?? null,
      categoryName:updated.category?.name ?? "Sem categoria",
      walletId:    updated.walletId,
      createdAt:   updated.createdAt.getTime(),
    });
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
    const txId = params.id;

    const existing = await prisma.transaction.findFirst({
      where: { id: txId, organizationId: claims.orgId },
    });

    if (!existing) {
      return NextResponse.json({ error: "Transação não encontrada" }, { status: 404 });
    }

    await prisma.transaction.delete({ where: { id: txId } });

    return NextResponse.json({ success: true });
  } catch (err: unknown) {
    const message = err instanceof Error ? err.message : "Erro";
    const status = message.includes("Token") ? 401 : 500;
    return NextResponse.json({ error: message }, { status });
  }
}
