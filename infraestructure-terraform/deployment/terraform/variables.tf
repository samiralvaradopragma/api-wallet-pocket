variable "aws_region" {
  type        = string
  default     = "us-east-2"
  description = "AWS Region to deploy the infrastructure"
}

variable "db_password" {
  type        = string
  sensitive   = true
  description = "Master password for the RDS PostgreSQL database"
}

variable "ecr_image_url" {
  type        = string
  description = "The URL of the ECR repository containing the wallet-api Docker image"
}

variable "ecs_exec_role_arn" {
  type        = string
  description = "The ARN of the IAM Role for ECS execution (Task Execution Role)"
}