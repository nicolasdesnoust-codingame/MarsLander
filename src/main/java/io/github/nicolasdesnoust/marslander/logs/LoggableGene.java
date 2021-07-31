package io.github.nicolasdesnoust.marslander.logs;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class LoggableGene {
	private int index;
	private int x;
	private int y;
}
