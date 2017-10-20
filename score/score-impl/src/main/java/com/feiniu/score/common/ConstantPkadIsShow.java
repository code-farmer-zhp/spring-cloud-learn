package com.feiniu.score.common;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
@Component
public class ConstantPkadIsShow {
	public final Set<String> HIDDEN_MRSTS;
	public final Set<String> SHOW_MRSTS;
	public final Set<String> SHOW_MRSTS_PARTNER;
	public final Set<String> HIDDEN_MRSTS_PARTNER;
	public ConstantPkadIsShow(String showPkads,String hiddenPkads,String showPkadsPartner,String hiddenPkadsPartner){
		HashSet<String> hiddenSets=new HashSet<>();
		String[] hiddenUis= hiddenPkads.split(",");
		Collections.addAll(hiddenSets, hiddenUis);
		HIDDEN_MRSTS = Collections.unmodifiableSet(hiddenSets);

		HashSet<String> showSets=new HashSet<>();
		String[] showUis= showPkads.split(",");
		Collections.addAll(showSets, showUis);
		SHOW_MRSTS = Collections.unmodifiableSet(showSets);

		HashSet<String> showSetsPar=new HashSet<>();
		String[] showUisPar= showPkadsPartner.split(",");
		Collections.addAll(showSetsPar, showUisPar);
		SHOW_MRSTS_PARTNER = Collections.unmodifiableSet(showSetsPar);

		HashSet<String> hiddSetsPar=new HashSet<>();
		String[] hiddenUisPar= hiddenPkadsPartner.split(",");
		Collections.addAll(hiddSetsPar, hiddenUisPar);
		HIDDEN_MRSTS_PARTNER = Collections.unmodifiableSet(hiddSetsPar);
	}
}
