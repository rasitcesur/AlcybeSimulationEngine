package alcybe.simulation.types;

public enum SetupStrategy {
	NoSetup,
	SetupAfterCycleEnds,
	SetupAfterEachOperation,
	SetupAfterSpecifiedAmount,
	SetupRegardingBufferCapacity,
	SetupRegardingCycleAndAmount
}
