package com.custardsource.scratchminder;

import android.annotation.SuppressLint;
import java.util.HashMap;
import java.util.Map;

public enum Avatar {
	caveman(R.drawable.caveman), dino_blue(R.drawable.dino_blue), dino_green(
			R.drawable.dino_green), dino_orange(R.drawable.dino_orange), pterodactyl(
			R.drawable.pterodactyl), remember_the_milk(
			R.drawable.remember_the_milk), user_iconshock_tiny_monsters_monster1(
			R.drawable.user_iconshock_tiny_monsters_monster1), user_iconshock_tiny_monsters_monster2(
			R.drawable.user_iconshock_tiny_monsters_monster2), user_iconshock_tiny_monsters_monster3(
			R.drawable.user_iconshock_tiny_monsters_monster3), user_iconshock_tiny_monsters_monster5(
			R.drawable.user_iconshock_tiny_monsters_monster5), user_iconshock_tiny_monsters_monster4(
			R.drawable.user_iconshock_tiny_monsters_monster4), user_fasticon_creaturecutes_blue(
			R.drawable.user_fasticon_creaturecutes_blue), user_fasticon_creaturecutes_bluepants(
			R.drawable.user_fasticon_creaturecutes_bluepants), user_fasticon_creaturecutes_greenblue(
			R.drawable.user_fasticon_creaturecutes_greenblue), user_fasticon_creaturecutes_greenbluepants(
			R.drawable.user_fasticon_creaturecutes_greenbluepants), user_fasticon_creaturecutes_green(
			R.drawable.user_fasticon_creaturecutes_green), user_fasticon_creaturecutes_greenpants(
			R.drawable.user_fasticon_creaturecutes_greenpants), user_fasticon_creaturecutes_grape(
			R.drawable.user_fasticon_creaturecutes_grape), user_fasticon_creaturecutes_grapepants(
			R.drawable.user_fasticon_creaturecutes_grapepants), user_fasticon_creaturecutes_orange(
			R.drawable.user_fasticon_creaturecutes_orange), user_fasticon_creaturecutes_orangepants(
			R.drawable.user_fasticon_creaturecutes_orangepants), user_fasticon_creaturecutes_red(
			R.drawable.user_fasticon_creaturecutes_red), user_fasticon_creaturecutes_redpants(
			R.drawable.user_fasticon_creaturecutes_redpants),

			icon_iconka_buddy_airhostess(R.drawable.icon_iconka_buddy_airhostess), 
			icon_iconka_buddy_alien(R.drawable.icon_iconka_buddy_alien), 
			icon_iconka_buddy_alieness(R.drawable.icon_iconka_buddy_alieness), 
			icon_iconka_buddy_angel(R.drawable.icon_iconka_buddy_angel), 
			icon_iconka_buddy_aphrodite(R.drawable.icon_iconka_buddy_aphrodite), 
			icon_iconka_buddy_astronaut(R.drawable.icon_iconka_buddy_astronaut), 
			icon_iconka_buddy_canary(R.drawable.icon_iconka_buddy_canary), 
			icon_iconka_buddy_captainess(R.drawable.icon_iconka_buddy_captainess), 
			icon_iconka_buddy_catwoman(R.drawable.icon_iconka_buddy_catwoman), 
			icon_iconka_buddy_contractor(R.drawable.icon_iconka_buddy_contractor), 
			icon_iconka_buddy_dandy(R.drawable.icon_iconka_buddy_dandy), 
			icon_iconka_buddy_devil(R.drawable.icon_iconka_buddy_devil), 
			icon_iconka_buddy_doctor(R.drawable.icon_iconka_buddy_doctor), 
			icon_iconka_buddy_fairy(R.drawable.icon_iconka_buddy_fairy), 
			icon_iconka_buddy_gangster(R.drawable.icon_iconka_buddy_gangster), 
			icon_iconka_buddy_king(R.drawable.icon_iconka_buddy_king), 
			icon_iconka_buddy_maid(R.drawable.icon_iconka_buddy_maid), 
			icon_iconka_buddy_nick(R.drawable.icon_iconka_buddy_nick), 
			icon_iconka_buddy_ninja(R.drawable.icon_iconka_buddy_ninja), 
			icon_iconka_buddy_nun(R.drawable.icon_iconka_buddy_nun), 
			icon_iconka_buddy_nurse(R.drawable.icon_iconka_buddy_nurse), 
			icon_iconka_buddy_officer(R.drawable.icon_iconka_buddy_officer), 
			icon_iconka_buddy_priest(R.drawable.icon_iconka_buddy_priest), 
			icon_iconka_buddy_queen(R.drawable.icon_iconka_buddy_queen), 
			icon_iconka_buddy_robot(R.drawable.icon_iconka_buddy_robot), 
			icon_iconka_buddy_robotess(R.drawable.icon_iconka_buddy_robotess), 
			icon_iconka_buddy_sportsman(R.drawable.icon_iconka_buddy_sportsman), 
			icon_iconka_buddy_teacher(R.drawable.icon_iconka_buddy_teacher), 
			
	user_iconshock_afro(R.drawable.user_iconshock_afro), user_iconshock_alien(
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
