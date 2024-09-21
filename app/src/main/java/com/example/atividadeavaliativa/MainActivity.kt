package com.example.atividadeavaliativa

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.atividadeavaliativa.ui.theme.AtividadeAvaliativaTheme
import java.math.BigDecimal
import com.google.gson.Gson



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AtividadeAvaliativaTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "tela1") {
        composable("tela1") { TelaCadastro(navController) }
        composable("tela2") { TelaLista(navController) }
        composable("telaDetalhes/{produtoJson}") { backStackEntry ->
            val produtoJson = backStackEntry.arguments?.getString("produtoJson") ?: ""
            TelaDetalhesProduto(navController, produtoJson)
        }
        composable("telaEstatisticas") { TelaEstatisticas(navController) } // Passando navController

    }
}

@Composable
fun TelaCadastro(navController: NavController) {
    var nome by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome do Produto") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Categoria") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = preco,
            onValueChange = { preco = it },
            label = { Text("Preço") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = quantidade,
            onValueChange = { quantidade = it },
            label = { Text("Quantidade em Estoque") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (nome.isBlank() || categoria.isBlank() || preco.isBlank() || quantidade.isBlank()) {
                    Toast.makeText(context, "Todos os campos são obrigatórios.", Toast.LENGTH_SHORT).show()
                } else {
                    val produto = Produto(
                        nome = nome,
                        categoria = categoria,
                        preco = BigDecimal(preco),
                        quantidadeEmEstoque = quantidade.toInt()
                    )
                    Produto.listaProdutos.add(produto)

                    nome = ""
                    categoria = ""
                    preco = ""
                    quantidade = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cadastrar")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("tela2") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver Lista de Produtos")
        }
    }
}

@Composable
fun TelaLista(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Lista de Produtos:", style = MaterialTheme.typography.titleMedium)
        LazyColumn {
            items(Produto.listaProdutos) { produto ->
                Row(modifier = Modifier.fillMaxWidth().clickable {
                    // Serializando o objeto produto em JSON
                    val produtoJson = Gson().toJson(produto)
                    navController.navigate("telaDetalhes/$produtoJson")
                }) {
                    Text(text = "${produto.nome} (${produto.quantidadeEmEstoque} unidades)", modifier = Modifier.weight(1f))
                    Button(onClick = {
                        val produtoJson = Gson().toJson(produto)
                        navController.navigate("telaDetalhes/$produtoJson")
                    }) {
                        Text("Detalhes")
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))


        Button(
            onClick = { navController.navigate("telaEstatisticas") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Estatísticas")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("tela1") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cadastrar Novo Produto")
        }
    }
}

@Composable
fun TelaEstatisticas(navController: NavController) {
    val valorTotalEstoque = Estoque.calcularValorTotalEstoque()
    val quantidadeTotalProdutos = Produto.listaProdutos.sumOf { it.quantidadeEmEstoque }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Estatísticas do Estoque", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Valor Total do Estoque: R$ ${valorTotalEstoque}", style = MaterialTheme.typography.bodyMedium)
        Text("Quantidade Total de Produtos: $quantidadeTotalProdutos", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("tela2") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Voltar")
        }
    }
}

@Composable
fun TelaDetalhesProduto(navController: NavController, produtoJson: String) {

    val produto = Gson().fromJson(produtoJson, Produto::class.java)

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Detalhes do Produto", style = MaterialTheme.typography.titleMedium)


        Text("Nome: ${produto.nome}")
        Text("Categoria: ${produto.categoria}")
        Text("Preço: R$ ${produto.preco}")
        Text("Quantidade em Estoque: ${produto.quantidadeEmEstoque} unidades")

        Spacer(modifier = Modifier.height(16.dp))


        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Voltar")
        }
    }
}


