import { redirect } from "next/navigation";

import { auth } from "@/auth";
import { Layout } from "@/components/Layout";
import { prisma } from "@/lib/prisma";

export default async function MainGroupLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const session = await auth();
  if (session?.user?.id) {
    const user = await prisma.user.findUnique({
      where: { id: session.user.id },
      select: { organizationId: true },
    });
    if (!user?.organizationId) {
      redirect("/onboarding");
    }
  }

  return <Layout>{children}</Layout>;
}
