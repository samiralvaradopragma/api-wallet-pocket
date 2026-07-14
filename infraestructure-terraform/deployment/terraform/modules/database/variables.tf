variable "vpc_id" { type = string }
variable "private_subnet_ids" { type = list(string) }
variable "ecs_security_group_id" { type = string }
variable "db_password" { type = string }
variable "public_subnet_ids" {
  description = "Lista de IDs de subredes públicas para la base de datos"
  type        = list(string)
}