"use server";

import bcrypt from "bcryptjs";

import { prisma } from "@/lib/prisma";
import {
  isNextRedirectError,
  logServerActionError,
  SERVER_ACTION_UNAVAILABLE_PT,
} from "@/lib/server-action-error";

export type SignUpResult =
  | { ok: true }
  | { ok: false; message: string };

export async function signUp(input: {
  name: string;
  email: string;
  password: string;
}): Promise<SignUpResult> {
  const email = input.email.trim().toLowerCase();
  const password = input.password;
  const name = input.name.trim();

  if (!name) {
    return { ok: false, message: "Informe seu nome." };
  }
  if (!email || !password) {
    return { ok: false, message: "Preencha e-mail e senha." };
  }
  if (password.length < 8) {
    return { ok: false, message: "A senha deve ter pelo menos 8 caracteres." };
  }

  try {
    const existing = await prisma.user.findUnique({
      where: { email },
      select: { id: true },
    });
    if (existing) {
      return { ok: false, message: "Este e-mail já está cadastrado." };
    }

    const hash = await bcrypt.hash(password, 12);
    await prisma.user.create({
      data: {
        email,
        password: hash,
        name,
      },
    });

    return { ok: true };
  } catch (e) {
    if (isNextRedirectError(e)) throw e;
    logServerActionError("signUp", e);
    return { ok: false, message: SERVER_ACTION_UNAVAILABLE_PT };
  }
}
