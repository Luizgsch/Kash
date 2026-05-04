"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import {
  Box,
  Button,
  TextField,
  Typography,
  Alert,
  Stack,
  InputAdornment,
} from "@mui/material";
import { Lock, Mail, User } from "lucide-react";

import { signUp } from "@/app/actions/auth";

export function RegisterForm() {
  const router = useRouter();
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [pending, setPending] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setMessage(null);
    setPending(true);
    const r = await signUp({
      name: name.trim(),
      email: email.trim(),
      password,
    });
    setPending(false);
    if (r.ok) {
      router.push("/login?registered=1");
      return;
    }
    setMessage(r.message);
  }

  return (
    <Box component="form" onSubmit={handleSubmit} sx={{ maxWidth: "sm", mx: "auto", width: "100%" }}>
      <Stack spacing={2}>
        <TextField
          fullWidth
          label="Nome"
          type="text"
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder="Seu nome"
          required
          slotProps={{
            input: {
              startAdornment: (
                <InputAdornment position="start">
                  <User size={18} />
                </InputAdornment>
              ),
            },
          }}
        />

        <TextField
          fullWidth
          label="E-mail"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          placeholder="seu@email.com"
          required
          slotProps={{
            input: {
              startAdornment: (
                <InputAdornment position="start">
                  <Mail size={18} />
                </InputAdornment>
              ),
            },
          }}
        />

        <TextField
          fullWidth
          label="Senha (mín. 8 caracteres)"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          slotProps={{
            input: {
              startAdornment: (
                <InputAdornment position="start">
                  <Lock size={18} />
                </InputAdornment>
              ),
            },
          }}
        />

        <Button
          fullWidth
          variant="contained"
          size="large"
          type="submit"
          disabled={pending}
          sx={{ bgcolor: "success.main" }}
        >
          {pending ? "Criando conta…" : "Criar conta"}
        </Button>

        {message && <Alert severity="warning">{message}</Alert>}

        <Typography variant="body2" align="center" sx={{ color: "text.secondary" }}>
          Já tem conta?{" "}
          <Link href="/login" style={{ color: "inherit", textDecoration: "none" }}>
            <Box component="span" sx={{ color: "primary.main", "&:hover": { textDecoration: "underline" } }}>
              Entrar
            </Box>
          </Link>
        </Typography>
      </Stack>
    </Box>
  );
}
