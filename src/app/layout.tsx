import type { Metadata, Viewport } from "next";
import { Providers } from "@/components/Providers";

export const metadata: Metadata = {
  title: "Kash - Gestão Financeira",
  description: "Controle financeiro multi-tenancy para empresas",
};

export const viewport: Viewport = {
  width: "device-width",
  initialScale: 1,
  maximumScale: 1,
  userScalable: false,
  themeColor: "#020617",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="pt-BR">
      <body>
        <Providers>{children}</Providers>
      </body>
    </html>
  );
}
