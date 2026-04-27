import { redirect } from "next/navigation";

import { auth } from "@/auth";
import { prisma } from "@/lib/prisma";

import { ProfileView } from "@/app/(main)/profile/ProfileView";

export default async function ProfilePage() {
  const session = await auth();
  if (!session?.user?.id) {
    redirect("/login");
  }

  const user = await prisma.user.findUnique({
    where: { id: session.user.id },
    select: {
      name: true,
      email: true,
      image: true,
      organization: { select: { name: true } },
    },
  });

  if (!user) {
    redirect("/login");
  }

  const email = user.email?.trim() || "—";
  const displayName =
    user.name?.trim() || (user.email ? user.email.split("@")[0] : null) || "Conta";
  const organizationName = user.organization?.name?.trim() || "—";
  const initialSource = (user.name?.trim() || user.email?.trim() || "?").trim();
  const initialLetter =
    initialSource.length > 0
      ? initialSource[0].toLocaleUpperCase("pt-BR")
      : "?";

  return (
    <ProfileView
      displayName={displayName}
      email={email}
      organizationName={organizationName}
      imageUrl={user.image}
      initialLetter={initialLetter}
    />
  );
}
