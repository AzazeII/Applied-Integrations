# Applied-Integrations [![CurseForge](http://cf.way2muchnoise.eu/301924.svg)](https://minecraft.curseforge.com/projects/applied-integrations) 

# About
Applied integrations is applied energitsics 2 addon, which adds many new features. One of main features is storing energy in storage cells

Copyright Azazell 2018-2019
[![License](https://img.shields.io/badge/License-MIT-red.svg?style=flat-square)](http://opensource.org/licenses/MIT)

# Features:
features marked with * is not currently working
features marked with ** is work in half, or unstable
features marked with *** is working only in 1.7.10 version

features marked with & will/already (be) disabled by default

Energy capabillities:
- Energy cells, allow you to store energy in cells
- Energy buses, allow you to manipulate energy storage
	- Energy import bus - allows you to import energy directly from energy source**
	- Energy export bus - allows you to export energy directly into energy source**
	- Energy storage bus - allows you to create ME inventory, from energy source**
	- Energy interface bus - allows you to import/export energy to buffer of interface bus
	
- Energy interface (block) same as interface bus, but have 6 buffers for each side*

Network features:
- ME Server, allows you to give connected networks restricted access to main server network***
- ME Logic bus, allows you to give connected network shared auto-craft space. Can handle up to 54 patterns**

Integrations with other mods:
Mana storage (botania):
- Mana cells, same as energy cells, but for mana
- Mana buses
	- Mana interface bus - allows you to import / export* mana to bufferof interface
	- Mana storage bus - allows to create ME inventory from any block which can handle mana*

P2P tunnels*:
Ember tunnel - allow you to transmit ember(embers) over p2p grid*
Starlight tunnel - allow you to transmit starlight(astral sorcery) over p2p grid*
Mana tunnel - allow you to transmit mana(botania) over p2p grid*

Black/White hole storage system &:
ME defence tower - Turret analoque of matter cannon. Can shoot with singularities from AE2*

Black hole - One of singularity types, which can be placed by shooting singularity from ME Defence tower. Has <not picked yet> chance to be created from singularity, otherwise white hole will be created

White hole - One of singularity types, which can be placed by shooting singularity from ME Defence tower. Has <not picked yet> chance to be created from singularity, otherwise black hole will be created

ME Pylon - Allows you to inject any type of matter (item,fluid,energy,mana) into black hole entagled with white hole
						And extract any type of matter from white hole entagled with black hole.
						Cable must be connected down to pylon
						
To entagle 2 holes together you need to shot two singularities from ME defnce tower in one tick*.
only white and black hole can be entagled

For developers:
 You can add own matter type to Black/white hole storage system, by typing this in your common proxy*:
 AIApi.instance().addHandlersForMEPylon(#yourHandlerForBlackHole#.class, #yourHandlerForWhiteHole#.class, AEApi.instance().storage().getStorageChannel(#yourStorageChannel#.class));
 
 black and white hole handlers must extend
 AppliedIntegrations.API.Storage.helpers.WhiteHoleSingularityInventoryHandler<#your IAEStack#>
 or
 AppliedIntegrations.API.Storage.helpers.BlackHoleSingularityInventoryHandler<#your IAEStack#>

