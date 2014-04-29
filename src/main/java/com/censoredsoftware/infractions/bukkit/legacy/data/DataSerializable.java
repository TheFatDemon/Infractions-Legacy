/*
 * Copyright 2014 Alexander Chauncey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.censoredsoftware.infractions.bukkit.legacy.data;

import java.util.Map;

@SuppressWarnings("unchecked")
public interface DataSerializable<K>
{
	K getId();

	/**
	 * Serialize the data held in the child class.
	 *
	 * @return Map of serialized data for the child class's current instance.
	 */
	Map<String, Object> serialize();
}
