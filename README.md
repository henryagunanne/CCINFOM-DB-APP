<!-----



Conversion time: 1.368 seconds.


Using this Markdown file:

1. Paste this output into your source file.
2. See the notes and action items below regarding this conversion run.
3. Check the rendered output (headings, lists, code blocks, tables) for proper
   formatting and use a linkchecker before you publish this page.

Conversion notes:

* Docs to Markdown version 1.0β44
* Sat Jul 19 2025 00:34:03 GMT-0700 (PDT)
* Source doc: CCINFOM Database Application Proposal
* Tables are currently converted to HTML tables.
----->


**        CCINFOM DATABASE APPLICATION PROPOSAL**

# Clothing Store Inventory and Sales Management System


### **Section 1.0 Group Composition**



* Agunanne, Henry  
* Adriano, Mark Luis
* Encallado, Edlynn Rei
* Manatad, Francinne

**Section 2.0 Why is this Database System important to be developed**

One cannot deny that relying on Excel to manage inventory, customer data, and sales often becomes unmanageable and prone to errors as the business grows bigger. Hence, having a dedicated database system will centralize these processes and enable real-time tracking, accurate sales recording, and automated reporting. This not only enhances operational efficiency but also provides the analytical tools needed to support informed, data-driven business decisions—capabilities that Excel alone cannot reliably offer.

**Section 3.0 Records Management**

The proposed database system will maintain four core entities: Product, Customer, Sales Representative, and Branch Records. Each of these components plays a vital role in the store’s operations and will be handled through dedicated record management modules. Detailed descriptions of each record are presented in the table below.


<table>
  <tr>
   <td><strong>Records</strong>
   </td>
   <td><strong>Fields</strong>
   </td>
   <td><strong>Assigned to</strong>
   </td>
  </tr>
  <tr>
   <td>Product Record Management
   </td>
   <td>
<ul>

<li>product_id</li>

<li>product_name</li>

<li>size</li>

<li>color</li>

<li>category</li>

<li>quantity_in_stock</li>

<li>unit_price</li>

<li>discontinued
<strong>Including</strong> viewing a product record and the list of customers who purchased it</li>
</ul>
   </td>
   <td>Henry Agunanne
   </td>
  </tr>
  <tr>
   <td>Customer Record Management
   </td>
   <td>
<ul>

<li><em>customer_id</em></li>

<li><em>first_name</em></li>

<li><em>last_name</em></li>

<li><em>email</em></li>

<li><em>gender</em></li>

<li><em>date_registered</em>
<strong>Including</strong> viewing a customer and the list of products they purchased</li>
</ul>
   </td>
   <td>Manatad, Francinne Kaye
   </td>
  </tr>
  <tr>
   <td>Sales Representative Record Management
   </td>
   <td>
<ul>

<li><em>sales_rep_id</em></li>

<li><em>name</em></li>

<li><em>branch_code</em></li>

<li><em>email</em></li>

<li><em>hire_date</em></li>

<li><em>active_status</em>
<strong>Including</strong> viewing a sales representative and their completed sales</li>
</ul>
   </td>
   <td>Encallado Edlynn Rei
   </td>
  </tr>
  <tr>
   <td>Branch Record Management
   </td>
   <td>
<ul>

<li><em>branch_code</em></li>

<li><em>branch_name</em></li>

<li><em>location</em></li>

<li><em>contact_number</em>
<strong>Including</strong> viewing a branch and the list of active sales representatives</li>
</ul>
   </td>
   <td>Manatad Francinne
   </td>
  </tr>
</table>


**Section 4.0 Transactions**


#### Transaction 1: Selling Clothing Items

*Assigned to: Encallado Edlynn Rei* \
Operations:



* Retrieve information and validate status if the sale is from a member
* Display available products by size and category
* Record the sale (type of payment, member information, sales rep, total amount, date)
* Record each item sold (product, quantity, unit price)
* Update stock quantity

Transaction 2: Restocking Products

*Assigned to: Manatad, Francinne* \
Operations:



* Choose supplier
* Select products to restock
* Input quantity and cost
* Record restocking entry
* Update stock levels


#### Transaction 3: Processing a Return

*Assigned to: Agunanne, Henry* \
Operations:



* Select past sale to return from
* Select items and quantities being returned
* Add return record and reason
* Update product stock (increase quantity)
* Generate return receipt


#### Transaction 4: Transferring Stock Between Branches

*Assigned to: Manatad, Francinne* \
Operations:



* Select source and destination branches
* Select products and quantity to transfer
* Deduct from source branch inventory
* Add to destination branch inventory
* Log stock transfer with date and reason


### **Section 5.0 Reports to be Generated**


<table>
  <tr>
   <td><strong>Report</strong>
   </td>
   <td><strong><em>Description</em></strong>
   </td>
   <td><strong>Assigned to</strong>
   </td>
  </tr>
  <tr>
   <td>Monthly Sales Report
   </td>
   <td>Total sales amount and quantity sold per day, grouped by product category, for a selected month and year
   </td>
   <td>Henry Agunanne
   </td>
  </tr>
  <tr>
   <td>Sales per Sales Representative Report
   </td>
   <td>Total sales amount per sales rep, grouped by branch, for a selected month
   </td>
   <td>Adriano Mark Luis
   </td>
  </tr>
  <tr>
   <td>Product Performance Report
   </td>
   <td>Total quantity sold and revenue per product, for a selected quarter, including average price sold
   </td>
   <td>Encallado Edlynn Rei
   </td>
  </tr>
  <tr>
   <td>Revenue per Branch Report
   </td>
   <td>Total revenue generated per branch for a selected month and year
   </td>
   <td>Manatad Francinne
   </td>
  </tr>
</table>

