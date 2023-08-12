package alcybe.simulation.types;

public enum DispatchingStrategy {
	CapacityUsage,
	FirstAvailable,
	HighestPriority,
	LowestPriority,
	PerformedTaskDuration,
	RealTime,
	TotalTaskDuration,
}
