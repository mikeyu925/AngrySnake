<template>
    <div class="container content-field">
        <div class="card">
            <div class="card-body">
                <!-- 要填充的内容都放在slot里面 -->
                <slot>
                </slot>
            </div>
        </div>
    </div>
</template>

<script>

</script>


<style scoped>
    div.content-field{
        margin-top: 40px;
        width: 400px;
    }
</style>