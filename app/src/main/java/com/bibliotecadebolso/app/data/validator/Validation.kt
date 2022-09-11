package com.bibliotecadebolso.app.data.validator

class Validation(
    private val listValidation: List<FieldValidation>
) {

    fun checkAllValidations(): List<ValidationError> {
        val errors: MutableList<ValidationError> = mutableListOf()
        listValidation.forEach {
            val validation = it.validate()
            if (validation != null) errors.add(validation)
        }
        return  errors.toList()
    }
}