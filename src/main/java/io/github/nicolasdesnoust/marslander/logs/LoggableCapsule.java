package io.github.nicolasdesnoust.marslander.logs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.github.nicolasdesnoust.marslander.core.Capsule;
import io.github.nicolasdesnoust.marslander.core.LandingState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter @Setter
@JsonInclude(Include.NON_NULL)
public class LoggableCapsule {
	private Integer index;
	private Integer x;
	private Integer y;
	private Double hSpeed;
	private Double vSpeed;
	private Double fuel;
	private Integer rotate;
	private LandingState landingState;
	
	public LoggableCapsule(Capsule capsule, int index, boolean fullCopy) {
		this.index = index;
		this.x = (int) Math.round(capsule.getPosition().getX());
		this.y = (int) Math.round(capsule.getPosition().getY());
		this.fuel = capsule.getFuel();
		
		if(fullCopy) {
			this.hSpeed = capsule.gethSpeed();
			this.vSpeed = capsule.getvSpeed();
			this.rotate = capsule.getRotate();
			this.landingState = capsule.getLandingState();
		}
	}

}
