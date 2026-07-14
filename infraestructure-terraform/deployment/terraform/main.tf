module "networking" {
  source       = "./modules/networking"
  vpc_cidr     = "10.0.0.0/16"
  public_cidrs = ["10.0.1.0/24", "10.0.2.0/24"]
  private_cidrs= ["10.0.3.0/24", "10.0.4.0/24"]
  azs          = ["us-east-2a", "us-east-2b"]
}

module "security" {
  source = "./modules/security"
  vpc_id = module.networking.vpc_id
}

module "database" {
  source             = "./modules/database"
  vpc_id             = module.networking.vpc_id
  private_subnet_ids = module.networking.private_subnet_ids
  ecs_security_group_id = module.security.ecs_security_group_id
  db_password        = var.db_password
}

module "compute" {
  source                 = "./modules/compute"
  vpc_id                 = module.networking.vpc_id
  public_subnet_ids      = module.networking.public_subnet_ids
  private_subnet_ids     = module.networking.private_subnet_ids
  alb_security_group_id  = module.security.alb_security_group_id
  ecs_security_group_id  = module.security.ecs_security_group_id
  db_endpoint            = module.database.db_endpoint
  ecr_image_url          = aws_ecr_repository.wallet_api_repo.repository_url
  ecs_exec_role_arn      = aws_iam_role.ecs_execution_role.arn
}

resource "aws_iam_role" "ecs_execution_role" {
  name = "wallet-ecs-execution-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action    = "sts:AssumeRole"
      Effect    = "Allow"
      Principal = { Service = "ecs-tasks.amazonaws.com" }
    }]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_execution" {
  role       = aws_iam_role.ecs_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_ecr_repository" "wallet_api_repo" {
  name                 = "wallet-pocket-api"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }
}