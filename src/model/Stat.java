package model;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public final class Stat {
	@NonNull
	private final int rank, level, xp;
}