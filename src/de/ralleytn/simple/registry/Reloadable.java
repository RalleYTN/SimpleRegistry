package de.ralleytn.simple.registry;

import java.io.IOException;

/**
 * Marks a class as reloadable.
 * @author Ralph Niemitz/RalleYTN(ralph.niemitz@gmx.de)
 * @version 1.1.0
 * @since 1.1.0
 */
public interface Reloadable {

	/**
	 * Reloads the object.
	 * @throws IOException if an error occurs
	 * @since 1.1.0
	 */
	public void reload() throws IOException;
}
