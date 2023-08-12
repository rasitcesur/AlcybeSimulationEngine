package alcybe.simulation.data.containers;

import alcybe.simulation.objects.TransferableElement;

public class TransferableElementContainer extends ElementContainer<TransferableElement>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	
	public TransferableElementContainer() {	}

	public TransferableElementContainer(TransferableElement transferableElement) {
		super.setElement(transferableElement);
	}

	
}
