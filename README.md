# PyCharm Variable Type Status  

This PyCharm plugin displays the **type of the variable under the caret** in the **status bar**. It enhances code navigation by providing **real-time type information** without requiring explicit type hints.  

## **🔹 How It Works**
- Listens for **caret movements** using `CaretListener`.  
- Retrieves the **PSI element** at the caret position.  
- Uses **`TypeEvalContext.userInitiated()`** to determine the variable's inferred type.  
- Updates the **status bar** dynamically as the user navigates.  

## **🔹 Why This Matters**
- **Improves developer efficiency** by quickly showing type information.  
- **Enhances code readability** without needing explicit type annotations.  
- **Lightweight & non-intrusive**, integrating seamlessly with PyCharm's UI.  

## **🔹 Technologies Used**
- **IntelliJ Platform SDK** (for PyCharm plugin development)  
- **Kotlin** (for plugin implementation)  
- **PSI (Program Structure Interface)** (for AST traversal & type resolution)  

## **🔹 Key Considerations**
- Proper **disposal of event listeners** to avoid memory leaks.  
- Efficient **type resolution** using JetBrains' recommended APIs.  
- Compliance with **IntelliJ Plugin Development best practices**.  

---
🚀 **This project showcases IntelliJ plugin development, Kotlin programming, and type inference handling, demonstrating my ability to work with IDE extensions and PSI-based analysis.**  
