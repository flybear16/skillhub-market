# SkillHub 本地开发启动指南

> 适用于本地端口 3000 / 8080 被其他项目（如 jeecg-ai）占用的情况。

## 端口映射

| 服务 | 端口 | 备注 |
|------|------|------|
| Frontend (Vite) | **3001** | 原 3000 被占 |
| Backend (Spring Boot) | **8081** | 原 8080 被占 |
| Scanner | **8000** | Docker |
| PostgreSQL | **15432** | Docker → 容器内 5432 |
| Redis | **16379** | Docker → 容器内 6379 |
| MinIO API | **19000** | Docker |
| MinIO Console | **19001** | Docker |

## 启动步骤

### 1. 启动依赖服务（Docker）

```bash
docker compose -p skillhub up -d --wait --remove-orphans
```

等所有容器 Healthy 后继续。

### 2. 启动后端

```bash
cd server && \
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:15432/skillhub \
java -jar skillhub-app/target/skillhub-app-0.1.0.jar \
  --spring.profiles.active=local \
  --server.port=8081 \
  --spring.data.redis.port=16379
```

等待出现 `Started SkillhubApplication` 日志。

验证：`curl -sf http://localhost:8081/actuator/health`

### 3. 启动前端

```bash
cd web && pnpm exec vite --host 0.0.0.0 --port 3001 --strictPort
```

验证：`curl -sf http://localhost:3001`

### 4. 登录

浏览器打开 http://localhost:3001

- 用户名: `admin`
- 密码: `ChangeMe!2026`

## 已做的配置变更

### `web/vite.config.ts`

Vite proxy 指向 8081（原 8080）：

```ts
proxy: {
  '/api': {
    target: 'http://localhost:8081',
    changeOrigin: true,
  },
  '/oauth2': {
    target: 'http://localhost:8081',
    changeOrigin: true,
  },
},
```

### 已知问题

| 问题 | 原因 | 当前 workaround |
|------|------|-----------------|
| `scripts/run-dev-app.sh` 缺失 | 已被删除 | 直接 `java -jar` 启动 |
| `application-local.yml` PG 端口写死 5432 | 未与 docker-compose (15432) 对齐 | 启动时传 `SPRING_DATASOURCE_URL` 覆盖 |
| `application-local.yml` Redis 端口写死 6379 | 未与 docker-compose (16379) 对齐 | 启动时传 `--spring.data.redis.port=16379` |
| Scanner 容器端口未映射 | 旧镜像无 port mapping | `docker compose up --force-recreate skill-scanner` |

## 一键启动（完整命令）

```bash
# 终端 1: 依赖服务
docker compose -p skillhub up -d --wait --remove-orphans

# 终端 2: 后端
cd server && \
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:15432/skillhub \
java -jar skillhub-app/target/skillhub-app-0.1.0.jar \
  --spring.profiles.active=local \
  --server.port=8081 \
  --spring.data.redis.port=16379

# 终端 3: 前端
cd web && pnpm exec vite --host 0.0.0.0 --port 3001 --strictPort
```

## 停止

```bash
# 停前端/后端: Ctrl+C 各自终端
# 停依赖服务:
docker compose -p skillhub down --remove-orphans
```
