package com.custardsource.scratchminder;

import android.annotation.SuppressLint;
import java.util.HashMap;
import java.util.Map;

public enum Avatar {
	caveman(R.drawable.caveman), dino_blue(R.drawable.dino_blue), dino_green(
			R.drawable.dino_green), dino_orange(R.drawable.dino_orange), pterodactyl(
			R.drawable.pterodactyl), remember_the_milk(
			R.drawable.remember_the_milk), user_iconshock_afro(
			R.drawable.user_iconshock_afro), user_iconshock_alien(
			R.drawable.user_iconshock_alien), user_iconshock_anciano(
			R.drawable.user_iconshock_anciano), user_iconshock_artista(
			R.drawable.user_iconshock_artista), user_iconshock_astronauta(
			R.drawable.user_iconshock_astronauta), user_iconshock_barbaman(
			R.drawable.user_iconshock_barbaman), user_iconshock_bombero(
			R.drawable.user_iconshock_bombero), user_iconshock_boxeador(
			R.drawable.user_iconshock_boxeador), user_iconshock_bruce_lee(
			R.drawable.user_iconshock_bruce_lee), user_iconshock_caradebolsa(
			R.drawable.user_iconshock_caradebolsa), user_iconshock_chavo(
			R.drawable.user_iconshock_chavo), user_iconshock_cientifica(
			R.drawable.user_iconshock_cientifica), user_iconshock_cientifico_loco(
			R.drawable.user_iconshock_cientifico_loco), user_iconshock_comisario(
			R.drawable.user_iconshock_comisario), user_iconshock_cupido(
			R.drawable.user_iconshock_cupido), user_iconshock_diabla(
			R.drawable.user_iconshock_diabla), user_iconshock_director(
			R.drawable.user_iconshock_director), user_iconshock_dreds(
			R.drawable.user_iconshock_dreds), user_iconshock_elsanto(
			R.drawable.user_iconshock_elsanto), user_iconshock_elvis(
			R.drawable.user_iconshock_elvis), user_iconshock_emo(
			R.drawable.user_iconshock_emo), user_iconshock_escafandra(
			R.drawable.user_iconshock_escafandra), user_iconshock_estilista(
			R.drawable.user_iconshock_estilista), user_iconshock_extraterrestre(
			R.drawable.user_iconshock_extraterrestre), user_iconshock_fisicoculturista(
			R.drawable.user_iconshock_fisicoculturista), user_iconshock_funky(
			R.drawable.user_iconshock_funky), user_iconshock_futbolista_brasilero(
			R.drawable.user_iconshock_futbolista_brasilero), user_iconshock_gay(
			R.drawable.user_iconshock_gay), user_iconshock_geisha(
			R.drawable.user_iconshock_geisha), user_iconshock_ghostbuster(
			R.drawable.user_iconshock_ghostbuster), user_iconshock_glamrock_singer(
			R.drawable.user_iconshock_glamrock_singer), user_iconshock_guerrero_chino(
			R.drawable.user_iconshock_guerrero_chino), user_iconshock_hiphopper(
			R.drawable.user_iconshock_hiphopper), user_iconshock_hombre_hippie(
			R.drawable.user_iconshock_hombre_hippie), user_iconshock_hotdog_man(
			R.drawable.user_iconshock_hotdog_man), user_iconshock_indio(
			R.drawable.user_iconshock_indio), user_iconshock_joker(
			R.drawable.user_iconshock_joker), user_iconshock_karateka(
			R.drawable.user_iconshock_karateka), user_iconshock_mago(
			R.drawable.user_iconshock_mago), user_iconshock_maori(
			R.drawable.user_iconshock_maori), user_iconshock_mario_barakus(
			R.drawable.user_iconshock_mario_barakus), user_iconshock_mascara_antigua(
			R.drawable.user_iconshock_mascara_antigua), user_iconshock_metalero(
			R.drawable.user_iconshock_metalero), user_iconshock_meteoro(
			R.drawable.user_iconshock_meteoro), user_iconshock_michelin(
			R.drawable.user_iconshock_michelin), user_iconshock_mimo(
			R.drawable.user_iconshock_mimo), user_iconshock_mister(
			R.drawable.user_iconshock_mister), user_iconshock_mounstrico1(
			R.drawable.user_iconshock_mounstrico1), user_iconshock_mounstrico2(
			R.drawable.user_iconshock_mounstrico2), user_iconshock_mounstrico3(
			R.drawable.user_iconshock_mounstrico3), user_iconshock_mounstrico4(
			R.drawable.user_iconshock_mounstrico4), user_iconshock_mounstruo(
			R.drawable.user_iconshock_mounstruo), user_iconshock_muerte(
			R.drawable.user_iconshock_muerte), user_iconshock_mujer_hippie(
			R.drawable.user_iconshock_mujer_hippie), user_iconshock_mujer_latina(
			R.drawable.user_iconshock_mujer_latina), user_iconshock_muneco_lego(
			R.drawable.user_iconshock_muneco_lego), user_iconshock_nena_afro(
			R.drawable.user_iconshock_muneco_lego);

	@SuppressLint("UseSparseArrays")
	private static final Map<Integer, Avatar> BY_DRAWABLE_IDS = new HashMap<Integer, Avatar>();
	static {
		for (Avatar avatar : Avatar.values()) {
			BY_DRAWABLE_IDS.put(avatar.drawableId, avatar);
		}
	}

	private int drawableId;

	Avatar(int drawableId) {
		this.drawableId = drawableId;

	}

	public int drawable() {
		return drawableId;
	}
}
