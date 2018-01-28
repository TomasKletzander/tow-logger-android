package cz.dmn.towlogger.core.io.api.models

import org.parceler.Parcel
import org.parceler.ParcelConstructor

@Parcel(Parcel.Serialization.BEAN)
data class Pilot @ParcelConstructor constructor(val name: String, val qualifications: Qualifications)
