package alcybe.simulation.data.containers;

import java.io.Serializable;

import alcybe.simulation.objects.TransferableElement;

public class AssemblyContainer implements Comparable<AssemblyContainer>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public TransferableElementContainer entityContainer = new TransferableElementContainer();
	
	public double amount=1, batchAmount=1, lotSize=1;
	public AssemblyContainer() {}
	
	public AssemblyContainer(TransferableElement element, double amount) {
		setEntity(element);
		setAmount(amount);
	}
	
	public AssemblyContainer(TransferableElement element, double amount, double batchAmount) {
		this(element, amount);
		setBatchAmount(batchAmount);
	}
	
	public AssemblyContainer(TransferableElementContainer elementContainer, double amount) {
		setEntityContainer(elementContainer);
		setAmount(amount);
	}
	
	public AssemblyContainer(TransferableElementContainer elementContainer, double amount, double batchAmount) {
		this(elementContainer, amount);
		setBatchAmount(batchAmount);
	}
	
	@Override
	public int compareTo(AssemblyContainer element) {
		return Long.compare(entityContainer.getElement().uniqueSequence, 
				element.entityContainer.getElement().uniqueSequence);
	}
	
	public TransferableElement getEntity() {
		return entityContainer.getElement();
	}

	public void setEntity(TransferableElement entity) {
		this.entityContainer.setElement(entity);
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getBatchAmount() {
		return batchAmount;
	}

	public void setBatchAmount(double batchAmount) {
		this.batchAmount = batchAmount;
	}

	public double getLotSize() {
		return lotSize;
	}

	public void setLotSize(double lotSize) {
		this.lotSize = lotSize;
	}
	
	public TransferableElementContainer getEntityContainer() {
		return entityContainer;
	}

	public void setEntityContainer(TransferableElementContainer entityContainer) {
		this.entityContainer = entityContainer;
	}

}
