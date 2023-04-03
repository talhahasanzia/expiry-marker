package io.github.talhahasanzia.expiry.processor

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.FileLocation
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.Location
import com.google.devtools.ksp.symbol.NonExistLocation
import io.github.talhahasanzia.expiry.annotation.Expiry
import java.text.SimpleDateFormat
import java.util.*
import java.util.Collections.emptyList
import kotlin.reflect.KClass


internal class ExpiryProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val annotatedElements: Sequence<KSAnnotated> =
            resolver.findAnnotations(Expiry::class)

        if (!annotatedElements.iterator().hasNext()) {
            return emptyList()
        }

        for (annotatedElement in annotatedElements) {
            val expiry = getExpiryAnnotation(annotatedElement)
            expiry?.let {
                val date = it.arguments.first().value as String
                val actualDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(date)
                val dateToday = Calendar.getInstance().time

                if (dateToday.after(actualDate)) {
                    val location = getElementLocation(annotatedElement.location)
                    environment.logger.error(
                        getFeatureExpiryMessage(
                            annotatedElement,
                            actualDate,
                            dateToday,
                            location
                        )
                    )
                }
            }
        }

        return emptyList()
    }

    private fun getFeatureExpiryMessage(
        annotatedElement: KSAnnotated,
        actualDate: Date?,
        dateToday: Date?,
        location: String
    ): String {
        return "@Expiry -> Feature expired: \"$annotatedElement\". " +
                "Expiry: $actualDate - Today: $dateToday." +
                " Can be found at : $location"
    }

    private fun getExpiryAnnotation(annotatedElement: KSAnnotated) =
        annotatedElement.annotations.find { it.shortName.asString() == Expiry::class.simpleName }

    private fun getElementLocation(location: Location): String {
        return when (location) {
            is FileLocation -> "${location.filePath}:${location.lineNumber}"
            NonExistLocation -> "Invalid location."
        }
    }

    private fun Resolver.findAnnotations(
        kClass: KClass<*>,
    ) = getSymbolsWithAnnotation(
        kClass.qualifiedName.toString()
    )
}