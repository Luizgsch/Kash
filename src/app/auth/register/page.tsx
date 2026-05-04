"use client";

import Link from "next/link";
import { Box, Container, Typography } from "@mui/material";

import { RegisterForm } from "@/app/auth/register/RegisterForm";

export default function RegisterPage() {
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
        <Box sx={{ textAlign: "center" }}>
          <Typography variant="h4" component="h1" sx={{ mb: 1 }}>
            Criar conta no Kash
          </Typography>
          <Typography variant="body2" sx={{ color: "text.secondary" }}>
            Você recebe uma organização e os espaços Loja e Pessoal com categorias prontas.
          </Typography>
        </Box>

        <RegisterForm />

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
