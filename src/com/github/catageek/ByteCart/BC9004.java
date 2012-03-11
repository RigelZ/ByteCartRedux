package com.github.catageek.ByteCart;

public class BC9004 extends BC9001 implements TriggeredIC {

	public BC9004(org.bukkit.block.Block block,
			org.bukkit.inventory.Inventory inv) {
		super(block, inv);
		this.netmask = 4;
		this.Name = "BC9004";
		this.FriendlyName = "4-station subnet";
		this.Buildtax = ByteCart.myPlugin.getConfig().getInt("buildtax." + this.Name);
		this.Permission = "bytecart." + this.Name;
	}

}
