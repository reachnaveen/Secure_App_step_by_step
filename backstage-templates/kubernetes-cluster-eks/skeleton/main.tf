resource "aws_vpc" "main" {
  cidr_block = "${{ values.vpc_cidr }}"
  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = {
    Name = "${{ values.cluster_name }}-vpc"
  }
}

resource "aws_subnet" "public" {
  count = 2
  vpc_id     = aws_vpc.main.id
  cidr_block = cidrsubnet(aws_vpc.main.cidr_block, 8, count.index)
  availability_zone = data.aws_availability_zones.available.names[count.index]

  tags = {
    Name = "${{ values.cluster_name }}-public-subnet-${count.index}"
  }
}

resource "aws_subnet" "private" {
  count = 2
  vpc_id     = aws_vpc.main.id
  cidr_block = cidrsubnet(aws_vpc.main.cidr_block, 8, count.index + 2)
  availability_zone = data.aws_availability_zones.available.names[count.index]

  tags = {
    Name = "${{ values.cluster_name }}-private-subnet-${count.index}"
  }
}

resource "aws_security_group" "cluster" {
  name_prefix = "${{ values.cluster_name }}-cluster-sg"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_eks_cluster" "main" {
  name     = "${{ values.cluster_name }}"
  role_arn = "arn:aws:iam::123456789012:role/eks-cluster-role" # Replace with your EKS cluster IAM role ARN

  vpc_config {
    subnet_ids = aws_subnet.private[*].id
    security_group_ids = [aws_security_group.cluster.id]
  }

  # Conceptual VPN/Private Link setup (replace with actual resources)
  # resource "aws_vpn_connection" "example" {
  #   vpn_gateway_id      = aws_vpn_gateway.main.id
  #   customer_gateway_id = aws_customer_gateway.main.id
  #   type                = "ipsec.1"
  # }

  # resource "aws_vpc_endpoint" "example" {
  #   vpc_id            = aws_vpc.main.id
  #   service_name      = "com.amazonaws.vpce.us-east-1.s3"
  #   vpc_endpoint_type = "Interface"
  #   subnet_ids        = aws_subnet.private[*].id
  #   security_group_ids = [aws_security_group.cluster.id]
  # }

  depends_on = [
    aws_iam_role_policy_attachment.example_AmazonEKSClusterPolicy,
    aws_iam_role_policy_attachment.example_AmazonEKSServicePolicy,
  ]

  tags = {
    Name = "${{ values.cluster_name }}"
    Environment = "EKS"
  }
}

data "aws_availability_zones" "available" {}

# Placeholder IAM roles and policies for EKS (replace with your actual IAM setup)
resource "aws_iam_role" "example" {
  name = "eks-cluster-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "eks.amazonaws.com"
        }
      },
    ]
  })
}

resource "aws_iam_role_policy_attachment" "example_AmazonEKSClusterPolicy" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSClusterPolicy"
  role       = aws_iam_role.example.name
}

resource "aws_iam_role_policy_attachment" "example_AmazonEKSServicePolicy" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSServicePolicy"
  role       = aws_iam_role.example.name
}
