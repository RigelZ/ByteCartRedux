package com.github.catageek.ByteCart.Signs;

import com.github.catageek.ByteCart.HAL.RegistryInput;
import com.github.catageek.ByteCart.Routing.AddressRouted;

public class BC7012 extends BC7013 implements Triggable {

	public BC7012(org.bukkit.block.Block block,
			org.bukkit.entity.Vehicle vehicle) {
		super(block, vehicle);
	}

	@Override
	protected String format(RegistryInput wire, AddressRouted InvAddress) {
		return ""+wire.getAmount()+"."
				+InvAddress.getTrack().getAmount()+"."
				+InvAddress.getStation().getAmount();
	}

	@Override
	public final String getName() {
		return "BC7012";
	}

	@Override
	public final String getFriendlyName() {
		return "setRegion";
	}
}
