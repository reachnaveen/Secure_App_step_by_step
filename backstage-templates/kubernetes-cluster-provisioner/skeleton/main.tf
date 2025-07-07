resource "aws_eks_cluster" "${{ values.cluster_name }}" {
  name     = "${{ values.cluster_name }}"
  role_arn = "arn:aws:iam::123456789012:role/eks-cluster-role"

  vpc_config {
    subnet_ids = ["subnet-0abcdef1234567890", "subnet-0fedcba9876543210"]
  }

  tags = {
    Name = "${{ values.cluster_name }}"
    Environment = "${{ values.cloud_provider }}"
    Region = "${{ values.region }}"
  }
}
