# Despliegue gratuito (Render + Neon)

Guia para publicar **Mercado Yuli** full-stack gratis:
- **Neon** = base de datos PostgreSQL gratuita.
- **Render** = servidor para la app Spring Boot (usa el `Dockerfile` de este repo).

---

## 1. Base de datos en Neon (gratis, permanente)

1. Entra a https://neon.tech y crea una cuenta (con Google o GitHub).
2. **Create Project** → nombre `mercadoyuli` → region la mas cercana.
3. En **Dashboard → Connection Details**, cambia el formato a **"Java" / "JDBC"**.
   Copia estos 3 datos (los usaras en Render):
   - **URL JDBC**: algo como
     `jdbc:postgresql://ep-xxxx.us-east-2.aws.neon.tech/neondb?sslmode=require`
   - **Usuario** (ej. `neondb_owner`)
   - **Contrasena**

> No necesitas crear tablas: la app las crea sola al arrancar (`ddl-auto=update`)
> y siembra los productos/categorias/admin automaticamente.

---

## 2. Subir el codigo (ya lo tienes en GitHub)

Asegurate de que este repo este actualizado en GitHub:
```
git add -A
git commit -m "Configura despliegue con Docker"
git push
```

---

## 3. App en Render (gratis)

1. Entra a https://render.com y crea cuenta con **GitHub**.
2. **New +** → **Web Service** → conecta este repositorio.
3. Render detecta el `Dockerfile` solo. Configura:
   - **Name**: `mercadoyuli` (tu URL sera `https://mercadoyuli.onrender.com`)
   - **Instance Type**: **Free**
4. Abre **Environment** y agrega estas **variables de entorno**:

| Variable | Valor |
|---|---|
| `SPRING_DATASOURCE_URL` | la URL JDBC de Neon (con `?sslmode=require`) |
| `SPRING_DATASOURCE_USERNAME` | el usuario de Neon |
| `SPRING_DATASOURCE_PASSWORD` | la contrasena de Neon |
| `MAIL_USERNAME` | tu correo Gmail |
| `MAIL_PASSWORD` | tu contrasena de aplicacion de Gmail |
| `APP_URL_BASE` | `https://mercadoyuli.onrender.com` (tu URL de Render) |
| `APP_JWT_SECRET` | una clave larga y secreta (minimo 32 caracteres) |
| `APP_JWT_EXPIRATION_MS` | `1800000` (30 min; sube el valor si quieres) |

5. **Create Web Service**. El primer build tarda ~3-5 min. Cuando termine,
   abre tu URL: `https://mercadoyuli.onrender.com`

---

## 4. Actualizar la app despues (muy facil)

Render esta conectado a tu GitHub: **cada `git push` a `main` redespliega solo**.
```
git add -A
git commit -m "mis cambios"
git push          # <- Render detecta el push y actualiza la app
```
La base de datos en Neon **no se borra** entre despliegues.

---

## Notas importantes

- **La app "se duerme"** tras ~15 min sin uso (plan free). El primer acceso
  despues tarda ~1 min en despertar. Para una exposicion: **abre la pagina
  2 min antes** para que ya este despierta.
- El **mapa, el QR y los graficos** funcionan igual (son del navegador).
- El **admin** sigue siendo `admin@mercadoyuli.com` / `admin123` (se siembra solo).
- El archivo local `secrets.properties` **no se sube** (esta en `.gitignore`);
  en produccion los secretos van en las variables de entorno de Render.
