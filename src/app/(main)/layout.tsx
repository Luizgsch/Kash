import { Layout } from "@/components/Layout";

export default async function MainGroupLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return <Layout>{children}</Layout>;
}
