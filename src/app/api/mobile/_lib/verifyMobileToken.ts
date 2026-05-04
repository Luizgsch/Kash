import { jwtVerify } from "jose";
import { NextRequest } from "next/server";

const SECRET = new TextEncoder().encode(
  process.env.AUTH_SECRET ?? process.env.NEXTAUTH_SECRET ?? "kash-mobile-secret"
);

export interface MobileTokenPayload {
  sub: string;   // userId
  orgId: string;
  walletId: string;
}

export async function verifyMobileToken(req: NextRequest): Promise<MobileTokenPayload> {
  const auth = req.headers.get("authorization") ?? "";
  const token = auth.startsWith("Bearer ") ? auth.slice(7) : "";

  if (!token) throw new Error("Token ausente");

  const { payload } = await jwtVerify(token, SECRET);

  if (!payload.sub || !payload["orgId"]) throw new Error("Token inválido");

  return payload as unknown as MobileTokenPayload;
}
