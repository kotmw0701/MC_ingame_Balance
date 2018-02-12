package jp.motlof.cb;

import org.bukkit.plugin.java.JavaPlugin;

import jp.motlof.cb.Balance.PriceParamType;

public class Main extends JavaPlugin {
	
	public static void main(String... args) {System.out.println(Balance.balance.getLoadTime() + Balance.balance.getPrice(PriceParamType.LAST) +" JPY");}
	
	@Override
	public void onEnable() {}
	
	@Override
	public void onDisable() {}
}
