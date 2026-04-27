import { redirect } from "next/navigation";

/** Compatibilidade: login canônico em `/login` (NextAuth `pages.signIn`). */
export default function SignInLegacyPage({
  searchParams,
}: {
  searchParams?: { registered?: string };
}) {
  const q = new URLSearchParams();
  if (searchParams?.registered === "1") q.set("registered", "1");
  const suffix = q.toString() ? `?${q.toString()}` : "";
  redirect(`/login${suffix}`);
}
