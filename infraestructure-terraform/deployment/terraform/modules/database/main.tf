resource "aws_db_subnet_group" "db_subnets" {
  name       = "wallet-db-subnet-group"
  subnet_ids = var.public_subnet_ids
}

resource "aws_security_group" "db_sg" {
  name   = "wallet-db-sg"
  vpc_id = var.vpc_id

  # 1. Permite conexión desde las tareas de ECS
  ingress {
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [var.ecs_security_group_id]
  }

  # 2. 🚨 NUEVO: Permite acceso a tu DBeaver local (Temporal para pruebas)
  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Aseguramos salida total
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_db_instance" "postgres" {
  allocated_storage      = 20
  engine                 = "postgres"
  engine_version         = "15"
  instance_class         = "db.t3.micro"
  db_name                = "walletdb"
  username               = "wallet_admin"
  password               = var.db_password
  db_subnet_group_name   = aws_db_subnet_group.db_subnets.name
  vpc_security_group_ids = [aws_security_group.db_sg.id]
  skip_final_snapshot    = true
  publicly_accessible    = true
}