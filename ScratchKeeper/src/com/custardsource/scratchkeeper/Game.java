package com.custardsource.scratchkeeper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.google.common.base.Joiner;

public class Game implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final List<Participant> participants = new ArrayList<Participant>();
	private String description;
	private int activePlayerIndex = 0;
	private long id = UUID.randomUUID().getLeastSignificantBits();

	public void addPlayer(Player player) {
		participants.add(new Participant(player));
	}

	public List<Participant> getParticipants() {
		return participants;
	}

	public int playerCount() {
		return participants.size();
	}

	public void recordScoreForActivePlayer(int score) {
		participants.get(activePlayerIndex).recordScore(score);
	}

	public void nextPlayer() {
		activePlayerIndex = (activePlayerIndex + 1) % participants.size();
		while (!participants.get(activePlayerIndex).active()) {
			activePlayerIndex = (activePlayerIndex + 1) % participants.size();

		}
	}

	public boolean isCurrentParticipant(Participant participant) {
		return participants.get(activePlayerIndex) == participant;
	}

	public List<Participant> getActiveParticipants() {
		List<Participant> active = new ArrayList<Participant>();
		for (Participant participant : participants) {
			if (participant.active()) {
				active.add(participant);
			}
		}
		return active;
	}

	public Participant partipantInActivePosition(int position) {
		int current = -1;
		for (Participant participant : participants) {
			if (participant.active()) {
				current++;
				if (current == position) {
					return participant;
				}
			}
		}
		return null;
	}

	public void leave(Participant participant) {
		participant.leave();
		if (isCurrentParticipant(participant)) {
			nextPlayer();
		}

	}

	public Participant participantInInactivePosition(int position) {
		// TODO-refactor
		int current = -1;
		for (Participant participant : participants) {
			if (!participant.active()) {
				current++;
				if (current == position) {
					return participant;
				}
			}
		}
		return null;
	}

	public void moveParticipantLast(Participant participant) {
		Participant active = participants.get(activePlayerIndex);
		participants.remove(participant);
		participants.add(participant);
		switchPlayTo(active);
	}

	public void moveParticipantNext(Participant participant) {
		Participant active = participants.get(activePlayerIndex);
		participants.remove(participant);
		participants.add(activePlayerIndex + 1, participant);
		switchPlayTo(active);
	}

	public void rejoin(Participant participant) {
		participant.join();
	}

	public int playingPositionOf(Participant participant) {
		return getActiveParticipants().indexOf(participant);
	}

	public void switchPlayTo(Participant participant) {
		activePlayerIndex = participants.indexOf(participant);
	}

	
	// TODO(beam)

	public Collection<Participant> getInactiveParticipants() {
		List<Participant> inactive = new ArrayList<Participant>();
		for (Participant participant : participants) {
			if (!participant.active()) {
				inactive.add(participant);
			}
		}
		return inactive;
	}

	public void resetScores() {
		for (Participant participant : participants) {
			participant.resetScore();
		}
	}

	public int totalScore() {
		int total = 0;
		for (Participant participant : participants) {
			total += participant.getScore();
		}
		return total;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}

	public String getNameList() {
		List<String> values = new ArrayList<String>(participants.size());
		for (Participant p : participants) {
			if (p.active()) {
				values.add(p.playerName());
			} else {
				values.add("(" + p.playerName() + ")");
			}
		}
		return Joiner.on(", ").join(values);
	}

	public Long id() {
		return this.id;
	}

}
