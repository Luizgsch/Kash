/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  /** Menos ruído em headers em produção (Vercel). */
  poweredByHeader: false,
};

export default nextConfig;
