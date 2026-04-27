import { auth } from "@/auth";
import { prisma } from "@/lib/prisma";

export type AppSessionContext = {
  userId: string;
  organizationId: string;
};

/**
 * Contexto de tenant para Server Actions e páginas: sessão real (Auth.js `auth()`,
 * equivalente ao antigo getServerSession) + organização no Postgres.
 */
export async function getSessionContext(): Promise<AppSessionContext | null> {
  const session = await auth();
  if (!session?.user?.id) return null;

  const user = await prisma.user.findUnique({
    where: { id: session.user.id },
    select: { id: true, organizationId: true },
  });

  if (!user?.organizationId) return null;
  return { userId: user.id, organizationId: user.organizationId };
}
