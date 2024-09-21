package com.example.atividadeavaliativa

import java.math.BigDecimal

data class Produto(
    val nome: String,
    val categoria: String,
    val preco: BigDecimal,
    val quantidadeEmEstoque: Int
) {
    init {
        require(nome.isNotBlank()) { "O nome do produto é obrigatório." }
        require(categoria.isNotBlank()) { "A categoria do produto é obrigatória." }
        require(preco >= BigDecimal.ZERO) { "O preço deve ser um valor numérico válido e positivo." }
        require(quantidadeEmEstoque >= 0) { "A quantidade em estoque deve ser um valor numérico válido e não pode ser negativa." }
    }

    companion object {
        val listaProdutos = mutableListOf<Produto>()

        fun adicionarProduto(produto: Produto) {
            listaProdutos.add(produto)
        }
    }
}

object Estoque {
    fun adicionarProduto(produto: Produto) {
        Produto.adicionarProduto(produto) // Adiciona à lista de produtos
    }

    fun calcularValorTotalEstoque(): BigDecimal {
        return Produto.listaProdutos.fold(BigDecimal.ZERO) { total, produto ->
            total.add(produto.preco.multiply(BigDecimal(produto.quantidadeEmEstoque)))
        }
    }

    fun listarProdutos(): List<Produto> {
        return Produto.listaProdutos
    }
}
