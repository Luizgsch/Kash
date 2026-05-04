"use client";

import Link from "next/link";
import { Box, Container, Typography, Alert } from "@mui/material";
import { LoginForm } from "@/app/login/LoginForm";

type LoginPageProps = {
  searchParams?: { registered?: string };
};

export default function LoginPage({ searchParams }: LoginPageProps) {
  const canEmailLogin = Boolean(process.env.RESEND_API_KEY?.trim());
  const justRegistered = searchParams?.registered === "1";

  return (
    <Container maxWidth="sm">
      <Box
        sx={{
          minHeight: "70vh",
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center",
          gap: 4,
          py: 4,
        }}
      >
        <Box sx={{ textAlign: "center", width: "100%" }}>
          <Typography variant="h4" component="h1" sx={{ mb: 1 }}>
            Entrar no Kash
          </Typography>
          <Typography variant="body2" sx={{ color: "text.secondary", mb: justRegistered || !canEmailLogin ? 2 : 0 }}>
            Google, e-mail e senha, ou link no e-mail (se configurado).
          </Typography>

          {justRegistered && (
            <Alert severity="success" sx={{ mt: 2 }}>
              Conta criada. Entre com seu e-mail e senha.
            </Alert>
          )}

          {!canEmailLogin && (
            <Alert severity="info" sx={{ mt: 2 }}>
              Entrada por e-mail: defina <code>RESEND_API_KEY</code> (e opcionalmente{" "}
              <code>EMAIL_FROM</code>).
            </Alert>
          )}
        </Box>

        <LoginForm canEmailLogin={canEmailLogin} />

        <Link href="/" style={{ textDecoration: "none" }}>
          <Typography
            variant="body2"
            sx={{
              color: "primary.main",
              "&:hover": { textDecoration: "underline" },
            }}
          >
            ← Voltar ao início
          </Typography>
        </Link>
      </Box>
    </Container>
  );
}
