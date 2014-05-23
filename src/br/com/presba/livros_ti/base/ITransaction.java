package br.com.presba.livros_ti.base;

public interface ITransaction {

	public void execute() throws Exception;
	public void updateView();
	public void refreshTextProgress(int textID);
}
