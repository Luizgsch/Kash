import { prisma } from "@/lib/prisma";
import { NextRequest, NextResponse } from "next/server";
import { verifyMobileToken } from "../../_lib/verifyMobileToken";

export async function DELETE(req: NextRequest, { params }: { params: { id: string } }) {
  try {
    const claims = await verifyMobileToken(req);

    const category = await prisma.category.findFirst({
      where: {
        id: params.id,
        wallet: { organizationId: claims.orgId },
      },
    });
    if (!category) {
      return NextResponse.json({ error: "Categoria não encontrada" }, { status: 404 });
    }

    await prisma.category.delete({ where: { id: params.id } });
    return new NextResponse(null, { status: 204 });
  } catch (err: unknown) {
    const message = err instanceof Error ? err.message : "Erro";
    return NextResponse.json({ error: message }, { status: message.includes("Token") ? 401 : 500 });
  }
}
