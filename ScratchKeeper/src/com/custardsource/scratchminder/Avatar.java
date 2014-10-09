package com.custardsource.scratchminder;

import android.annotation.SuppressLint;
import java.util.HashMap;
import java.util.Map;

public enum Avatar {
	caveman(R.drawable.caveman),
	dino_blue(R.drawable.dino_blue),
	dino_green(R.drawable.dino_green),
	dino_orange(R.drawable.dino_orange),
	pterodactyl(R.drawable.pterodactyl),
	remember_the_milk(R.drawable.remember_the_milk),
	user_iconshock_tiny_monsters_monster1(
			R.drawable.user_iconshock_tiny_monsters_monster1),
	user_iconshock_tiny_monsters_monster2(
			R.drawable.user_iconshock_tiny_monsters_monster2),
	user_iconshock_tiny_monsters_monster3(
			R.drawable.user_iconshock_tiny_monsters_monster3),
	user_iconshock_tiny_monsters_monster5(
			R.drawable.user_iconshock_tiny_monsters_monster5),
	user_iconshock_tiny_monsters_monster4(
			R.drawable.user_iconshock_tiny_monsters_monster4),
	user_fasticon_creaturecutes_blue(
			R.drawable.user_fasticon_creaturecutes_blue),
	user_fasticon_creaturecutes_bluepants(
			R.drawable.user_fasticon_creaturecutes_bluepants),
	user_fasticon_creaturecutes_greenblue(
			R.drawable.user_fasticon_creaturecutes_greenblue),
	user_fasticon_creaturecutes_greenbluepants(
			R.drawable.user_fasticon_creaturecutes_greenbluepants),
	user_fasticon_creaturecutes_green(
			R.drawable.user_fasticon_creaturecutes_green),
	user_fasticon_creaturecutes_greenpants(
			R.drawable.user_fasticon_creaturecutes_greenpants),
	user_fasticon_creaturecutes_grape(
			R.drawable.user_fasticon_creaturecutes_grape),
	user_fasticon_creaturecutes_grapepants(
			R.drawable.user_fasticon_creaturecutes_grapepants),
	user_fasticon_creaturecutes_orange(
			R.drawable.user_fasticon_creaturecutes_orange),
	user_fasticon_creaturecutes_orangepants(
			R.drawable.user_fasticon_creaturecutes_orangepants),
	user_fasticon_creaturecutes_red(R.drawable.user_fasticon_creaturecutes_red),
	user_fasticon_creaturecutes_redpants(
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

	user_animal_berube_alligator(R.drawable.user_animal_berube_alligator),
	user_animal_berube_ant(R.drawable.user_animal_berube_ant),
	user_animal_berube_bat(R.drawable.user_animal_berube_bat),
	user_animal_berube_bear(R.drawable.user_animal_berube_bear),
	user_animal_berube_bee(R.drawable.user_animal_berube_bee),
	user_animal_berube_bird(R.drawable.user_animal_berube_bird),
	user_animal_berube_bull(R.drawable.user_animal_berube_bull),
	user_animal_berube_bulldog(R.drawable.user_animal_berube_bulldog),
	user_animal_berube_butterfly(R.drawable.user_animal_berube_butterfly),
	user_animal_berube_cat(R.drawable.user_animal_berube_cat),
	user_animal_berube_chicken(R.drawable.user_animal_berube_chicken),
	user_animal_berube_cow(R.drawable.user_animal_berube_cow),
	user_animal_berube_crab(R.drawable.user_animal_berube_crab),
	user_animal_berube_crocodile(R.drawable.user_animal_berube_crocodile),
	user_animal_berube_deer(R.drawable.user_animal_berube_deer),
	user_animal_berube_dog(R.drawable.user_animal_berube_dog),
	user_animal_berube_donkey(R.drawable.user_animal_berube_donkey),
	user_animal_berube_duck(R.drawable.user_animal_berube_duck),
	user_animal_berube_eagle(R.drawable.user_animal_berube_eagle),
	user_animal_berube_elephant(R.drawable.user_animal_berube_elephant),
	user_animal_berube_fish(R.drawable.user_animal_berube_fish),
	user_animal_berube_fox(R.drawable.user_animal_berube_fox),
	user_animal_berube_frog(R.drawable.user_animal_berube_frog),
	user_animal_berube_giraffe(R.drawable.user_animal_berube_giraffe),
	user_animal_berube_gorilla(R.drawable.user_animal_berube_gorilla),
	user_animal_berube_hippo(R.drawable.user_animal_berube_hippo),
	user_animal_berube_horse(R.drawable.user_animal_berube_horse),
	user_animal_berube_insect(R.drawable.user_animal_berube_insect),
	user_animal_berube_lion(R.drawable.user_animal_berube_lion),
	user_animal_berube_monkey(R.drawable.user_animal_berube_monkey),
	user_animal_berube_moose(R.drawable.user_animal_berube_moose),
	user_animal_berube_mouse(R.drawable.user_animal_berube_mouse),
	user_animal_berube_owl(R.drawable.user_animal_berube_owl),
	user_animal_berube_panda(R.drawable.user_animal_berube_panda),
	user_animal_berube_penguin(R.drawable.user_animal_berube_penguin),
	user_animal_berube_pig(R.drawable.user_animal_berube_pig),
	user_animal_berube_rabbit(R.drawable.user_animal_berube_rabbit),
	user_animal_berube_rhino(R.drawable.user_animal_berube_rhino),
	user_animal_berube_rooster(R.drawable.user_animal_berube_rooster),
	user_animal_berube_shark(R.drawable.user_animal_berube_shark),
	user_animal_berube_sheep(R.drawable.user_animal_berube_sheep),
	user_animal_berube_snake(R.drawable.user_animal_berube_snake),
	user_animal_berube_tiger(R.drawable.user_animal_berube_tiger),
	user_animal_berube_turkey(R.drawable.user_animal_berube_turkey),
	user_animal_berube_turtle(R.drawable.user_animal_berube_turtle),
	user_animal_berube_wolf(R.drawable.user_animal_berube_wolf);
	
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
