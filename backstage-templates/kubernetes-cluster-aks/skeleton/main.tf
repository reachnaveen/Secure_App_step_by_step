resource "azurerm_resource_group" "main" {
  name     = "${{ values.resource_group_name }}"
  location = "${{ values.location }}"
}

resource "azurerm_virtual_network" "main" {
  name                = "${{ values.cluster_name }}-vnet"
  address_space       = ["${{ values.vnet_address_prefix }}"]
  location            = azurerm_resource_group.main.location
  resource_group_name = azurerm_resource_group.main.name
}

resource "azurerm_subnet" "internal" {
  name                 = "${{ values.cluster_name }}-subnet"
  resource_group_name  = azurerm_resource_group.main.name
  virtual_network_name = azurerm_virtual_network.main.name
  address_prefixes     = [cidrsubnet(azurerm_virtual_network.main.address_space[0], 8, 0)]
}

resource "azurerm_network_security_group" "aks_nsg" {
  name                = "${{ values.cluster_name }}-aks-nsg"
  location            = azurerm_resource_group.main.location
  resource_group_name = azurerm_resource_group.main.name

  security_rule {
    name                       = "Allow_SSH"
    priority                   = 100
    direction                  = "Inbound"
    access                     = "Allow"
    protocol                   = "Tcp"
    source_port_range          = "*"
    destination_port_range     = "22"
    source_address_prefix      = "*"
    destination_address_prefix = "*"
  }
}

resource "azurerm_kubernetes_cluster" "main" {
  name                = "${{ values.cluster_name }}"
  location            = azurerm_resource_group.main.location
  resource_group_name = azurerm_resource_group.main.name
  dns_prefix          = "${{ values.cluster_name }}-aks"

  default_node_pool {
    name       = "default"
    node_count = 1
    vm_size    = "Standard_DS2_v2"
    vnet_subnet_id = azurerm_subnet.internal.id
  }

  identity {
    type = "SystemAssigned"
  }

  # Conceptual VPN/Private Link setup (replace with actual resources)
  # resource "azurerm_virtual_network_gateway" "example" {
  #   name                = "${{ values.cluster_name }}-vnet-gateway"
  #   location            = azurerm_resource_group.main.location
  #   resource_group_name = azurerm_resource_group.main.name
  #   type                = "Vpn"
  #   vpn_type            = "RouteBased"
  #   sku                 = "VpnGw1"
  # }

  # resource "azurerm_private_endpoint" "example" {
  #   name                = "${{ values.cluster_name }}-private-endpoint"
  #   location            = azurerm_resource_group.main.location
  #   resource_group_name = azurerm_resource_group.main.name
  #   subnet_id           = azurerm_subnet.internal.id

  #   private_service_connection {
  #     name                           = "${{ values.cluster_name }}-psc"
  #     private_ip_address_enabled     = false
  #     is_manual_connection           = false
  #     private_connection_resource_id = azurerm_kubernetes_cluster.main.id
  #   }
  # }

  tags = {
    Environment = "AKS"
  }
}
