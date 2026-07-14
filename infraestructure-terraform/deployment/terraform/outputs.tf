output "alb_dns_name" {
  value       = module.compute.alb_dns_name
  description = "The public URL of your Virtual Wallet API Gateway"
}

output "database_endpoint" {
  value       = module.database.db_endpoint
  description = "The connection endpoint for the PostgreSQL instance"
}

output "ecr_repository_url" {
  value       = aws_ecr_repository.wallet_api_repo.repository_url
  description = "Usa esta URL exacta para autenticar Docker y subir tu imagen de la API"
}