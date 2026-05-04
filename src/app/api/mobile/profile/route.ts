import { prisma } from "@/lib/prisma";
import { NextRequest, NextResponse } from "next/server";
import { verifyMobileToken } from "../_lib/verifyMobileToken";

export async function GET(req: NextRequest) {
  try {
    const claims = await verifyMobileToken(req);

    const user = await prisma.user.findUnique({
      where: { id: claims.sub },
      select: { id: true, name: true, email: true, image: true, role: true },
    });

    if (!user) return NextResponse.json({ error: "Usuário não encontrado." }, { status: 404 });

    return NextResponse.json(user);
  } catch (err: unknown) {
    const message = err instanceof Error ? err.message : "Erro";
    const status = message.includes("Token") ? 401 : 500;
    return NextResponse.json({ error: message }, { status });
  }
}

export async function PATCH(req: NextRequest) {
  try {
    const claims = await verifyMobileToken(req);
    const body = await req.json();

    const name = (body.name ?? "").trim().slice(0, 120);
    if (!name) return NextResponse.json({ error: "Nome obrigatório." }, { status: 400 });

    const updated = await prisma.user.update({
      where: { id: claims.sub },
      data: { name },
      select: { id: true, name: true, email: true, image: true, role: true },
    });

    return NextResponse.json(updated);
  } catch (err: unknown) {
    const message = err instanceof Error ? err.message : "Erro";
    const status = message.includes("Token") ? 401 : 500;
    return NextResponse.json({ error: message }, { status });
  }
}
